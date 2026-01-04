# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.10.2)

set(TARGET_LIB_NAME "sys_mark")
set(NATIVE_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR})

list(APPEND SOURCE_DIR_INCLUDE
        ${NATIVE_SOURCE_DIR}
)

file(GLOB SOURCE_DIR_ROOT
        "${NATIVE_SOURCE_DIR}/*.cpp"
        "${NATIVE_SOURCE_DIR}/*.hpp"
        "${NATIVE_SOURCE_DIR}/*.cc"
)
# 头文件
include_directories(
        ${SOURCE_DIR_INCLUDE}
)

add_library(${TARGET_LIB_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        ${SOURCE_DIR_ROOT})

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${TARGET_LIB_NAME}
        # List libraries link to the target library
        android
        log)