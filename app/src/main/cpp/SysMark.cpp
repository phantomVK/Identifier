#include <jni.h>
#include <cstdio>
#include <cstdint>
#include <string>
#include <sys/stat.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_phantomvk_identifier_app_main_SysMark_getBootMark(JNIEnv *env, jobject) {
    FILE *fp = fopen("/proc/sys/kernel/random/boot_id", "r");
    if (fp == nullptr) {
        return env->NewStringUTF("");
    }

    int index = 0;
    char boot[37];

    while (index < 37) {
        int c = fgetc(fp);
        if (c == EOF) {
            fclose(fp);
            return env->NewStringUTF("");
        }
        boot[index++] = static_cast<char>(c);
    }

    fclose(fp);

    if (boot[36] != '\n') {
        return env->NewStringUTF("");
    }

    boot[36] = '\0';
    return env->NewStringUTF(boot);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_phantomvk_identifier_app_main_SysMark_getUpdateMark(JNIEnv *env, jobject) {
    struct stat sb{};
    if (stat("/data/data", &sb) == -1) {
        return env->NewStringUTF("");
    }

    const auto seconds = static_cast<int64_t>(sb.st_atim.tv_sec);
    const auto nanoseconds = static_cast<int64_t>(sb.st_atim.tv_nsec);

    char fraction[10];
    snprintf(fraction, 10, "%09lld", static_cast<long long>(nanoseconds));

    std::string result = std::to_string(seconds) + "." + fraction;
    return env->NewStringUTF(result.c_str());
}