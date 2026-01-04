#include <jni.h>
#include <string>
#include <sys/stat.h>

const int BOOT_ID_LENGTH = 37;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_phantomvk_identifier_app_main_SysMarkJni_getUpdateMark(JNIEnv *env, jobject) {
    struct stat sb;
    int updates{0};
    int updatens{0};
    if (stat("/data/data", &sb) == -1) {
        // 获取失败
    } else {
        updatens = (int) sb.st_atim.tv_nsec;
        updates = (int) sb.st_atim.tv_sec;
    }
    std::string idRes = std::to_string(updates) + "." + std::to_string(updatens);
    return env->NewStringUTF(idRes.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_phantomvk_identifier_app_main_SysMarkJni_getBootMark(JNIEnv *env, jobject) {
    FILE *fp = fopen("/proc/sys/kernel/random/boot_id", "r");
    char boot[BOOT_ID_LENGTH];
    int i = 0;
    if (fp == NULL) {
        // 获取失败
    } else {
        unsigned char c;

        while (i < BOOT_ID_LENGTH) {
            c = fgetc(fp);
            boot[i] = c;
            i = i + 1;
        }
        if (ferror(fp)) {
            // 获取失败
        }

        if (i == 37) {
            // 字符串末尾用'\0'覆盖，避免异常字符令c_str()崩溃
            boot[36] = '\0';
            std::string str_boot = boot;
            return env->NewStringUTF(str_boot.c_str());
        }
    }

    // 不满36位就当做异常情况直接返回空串
    char errorBoot[]{'\0'};
    std::string str_boot = errorBoot;
    return env->NewStringUTF(str_boot.c_str());
}