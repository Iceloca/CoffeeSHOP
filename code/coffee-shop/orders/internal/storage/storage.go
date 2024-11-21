package storage

import "errors"

var (
	ErrOrdersNotFound = errors.New("orders for this user not found")
)
