cmake_minimum_required(VERSION 3.10.2)

project("coffee")

add_library(
        password_validator
        SHARED
        /home/iceloca/AndroidStudioProjects/coffee/app/src/main/cpp/jni_password_validator.c
)

find_library(
        log-lib
        log
)

target_link_libraries(
        password_validator
        ${log-lib}
)