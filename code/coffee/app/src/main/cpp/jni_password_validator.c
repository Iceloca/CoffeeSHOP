#include <jni.h>
#include <string.h>


JNIEXPORT jboolean JNICALL
Java_com_example_coffee_SignUpActivity_isPasswordValid(
        JNIEnv *env,
        jobject obj,
        jstring password) {
    const char *nativePassword = (*env)->GetStringUTFChars(env, password, 0);
    int length = strlen(nativePassword);
    (*env)->ReleaseStringUTFChars(env, password, nativePassword);
    return (length > 8) ? JNI_TRUE : JNI_FALSE;
}