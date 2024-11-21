package sqlite

import (
	"database/sql"
	"errors"
	"fmt"
	"github.com/mattn/go-sqlite3"
	"sso/internal/storage"
)

type Storage struct {
	db *sql.DB
}

func New(storagePath string) (*Storage, error) {
	const op = "storage.sqlite.new"
	db, err := sql.Open("sqlite3", storagePath)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	stmt, err := db.Prepare(`
		CREATE TABLE IF NOT EXISTS user(
    		id INTEGER PRIMARY KEY ,
    		EMAIL TEXT NOT NULL UNIQUE,
		    PASS_HASH BLOB NOT NULL);
		CREATE INDEX IF NOT EXISTS idx_alias ON url(alias);
		)
	`)

	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	_, err = stmt.Exec()
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	return &Storage{db: db}, nil
}
func (s *Storage) UpdatePassword(login string, password []byte, passwordNew []byte) error {
	const op = "storage.sqlite.UpdateURL"

	stmt, err := s.db.Prepare("UPDATE user SET (email, pass_hash)  = (?, ?) WHERE email=? AND pass_hash=?")
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	_, err = stmt.Exec(login, passwordNew, login, password)
	if err != nil {
		if sqliteErr, ok := err.(sqlite3.Error); ok && sqliteErr.ExtendedCode == sqlite3.ErrConstraintUnique {
			return fmt.Errorf("%s: %w", op, storage.ErrUserExists)
		}
		return fmt.Errorf("%s: %w", op, err)
	}
	return nil
}
func (s *Storage) SaveUser(login string, password []byte) error {
	const op = "storage.sqlite.SaveUser"

	stmt, err := s.db.Prepare("INSERT INTO user(email, pass_hash) VALUES(?, ?)")
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	_, err = stmt.Exec(login, password)
	if err != nil {
		//TODO : refactor this
		if sqliteErr, ok := err.(sqlite3.Error); ok && sqliteErr.ExtendedCode == sqlite3.ErrConstraintUnique {
			return fmt.Errorf("%s: %w", op, storage.ErrUserExists)
		}
		return fmt.Errorf("%s: %w", op, err)
	}
	return nil
}

func (s *Storage) GetPassHash(email string) ([]byte, error) {
	const op = "storage.sqlite.GetURL"

	stmt, err := s.db.Prepare("SELECT pass_hash FROM user WHERE email = ?")
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}
	var resPassword []byte
	err = stmt.QueryRow(email).Scan(&resPassword)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, storage.ErrUserNotFound
		}
		return nil, fmt.Errorf("%s: %w", op, err)
	}
	return resPassword, nil
}

func (s *Storage) DeleteUser(email string) error {
	const op = "storage.sqlite.DeleteURL"

	stmt, err := s.db.Prepare("DELETE FROM user WHERE email = ?")
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}
	_, err = stmt.Exec(email)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}
	return nil
}
