package com.huawei.hmf.tasks;

public abstract class Task<TResult> {
    public abstract Task<TResult> addOnSuccessListener(OnSuccessListener<TResult> var1);
    public abstract Task<TResult> addOnFailureListener(OnFailureListener var1);
}
