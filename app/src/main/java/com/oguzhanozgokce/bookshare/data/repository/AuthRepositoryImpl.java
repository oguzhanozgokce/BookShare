package com.oguzhanozgokce.bookshare.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.data.SafeCall;
import com.oguzhanozgokce.bookshare.data.datastore.SessionManager;
import com.oguzhanozgokce.bookshare.domain.AuthRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;
    private final SessionManager sessionManager;

    @Inject
    public AuthRepositoryImpl(FirebaseAuth firebaseAuth, FirebaseFirestore db, SessionManager sessionManager) {
        this.firebaseAuth = firebaseAuth;
        this.db = db;
        this.sessionManager = sessionManager;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Result<String, FirebaseError> registerUser(String name, String email, String password) {
        return SafeCall.safeCall(() -> Executors.newSingleThreadExecutor().submit(() -> {
            Task<AuthResult> task = firebaseAuth.createUserWithEmailAndPassword(email, password);
            Tasks.await(task);

            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String uid = firebaseUser.getUid();

            // Basit user document oluştur
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);
            userData.put("listings", new HashMap<String, Object>());
            userData.put("savedListings", new HashMap<String, Boolean>());

            Task<Void> saveTask = db.collection("users")
                    .document(uid)
                    .set(userData);
            Tasks.await(saveTask);
            sessionManager.saveSession(uid, email, password);
            return uid;
        }).get());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Result<String, FirebaseError> loginUser(String email, String password) {
        return SafeCall.safeCall(() -> {
            Task<AuthResult> task = firebaseAuth.signInWithEmailAndPassword(email, password);
            Tasks.await(task);
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String uid = user.getUid();
            sessionManager.saveSession(uid, email, password);
            return uid;
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Result<String, FirebaseError> resetPassword(String email) {
        return SafeCall.safeCall(() -> {
            Task<Void> task = firebaseAuth.sendPasswordResetEmail(email);
            Tasks.await(task);
            return "Password reset email sent.";
        });
    }

    @Override
    public String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
