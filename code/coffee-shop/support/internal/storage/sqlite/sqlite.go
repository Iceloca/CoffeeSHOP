package sqlite

import (
	"database/sql"
	"errors"
	"fmt"
	"github.com/mattn/go-sqlite3"
	"support/internal/storage"
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
		CREATE TABLE IF NOT EXISTS ticket(
    		id INTEGER PRIMARY KEY ,
    		EMAIL TEXT NOT NULL ,
		    MESSAGE TEXT NOT NULL);
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

func (s *Storage) SaveTicket(email string, message string) error {
	const op = "storage.sqlite.SaveTicket"

	stmt, err := s.db.Prepare("INSERT INTO ticket(email, message) VALUES(?, ?)")
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	_, err = stmt.Exec(email, message)
	if err != nil {
		//TODO : refactor this
		if sqliteErr, ok := err.(sqlite3.Error); ok && sqliteErr.ExtendedCode == sqlite3.ErrConstraintUnique {
			return fmt.Errorf("%s: %w", op, storage.ErrUserExists)
		}
		return fmt.Errorf("%s: %w", op, err)
	}
	return nil
}

func (s *Storage) GetTicket(email int) (string, string, error) {
	const op = "storage.sqlite.GetTicket"

	stmt, err := s.db.Prepare("SELECT email,message FROM ticket WHERE email = ?")
	if err != nil {
		return "", "", fmt.Errorf("%s: %w", op, err)
	}
	var resEmail, resMessage string
	err = stmt.QueryRow(email).Scan(&resEmail, &resMessage)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return "", "", storage.ErrUserNotFound
		}
		return "", "", fmt.Errorf("%s: %w", op, err)
	}
	return resEmail, resMessage, nil
}

func (s *Storage) DeleteTicket(id int) error {
	const op = "storage.sqlite.DeleteTicket"

	stmt, err := s.db.Prepare("DELETE FROM ticket WHERE id = ?")
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}
	_, err = stmt.Exec(id)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}
	return nil
}
