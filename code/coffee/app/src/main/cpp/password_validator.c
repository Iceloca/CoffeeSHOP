#include <stdbool.h>

bool is_password_valid(const char* password) {
    int length = 0;
    while (password[length] != '\0') {
        length++;
    }
    return length > 8;
}