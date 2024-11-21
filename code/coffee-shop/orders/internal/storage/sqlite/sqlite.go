package sqlite

import (
	"database/sql"
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"orders/internal/storage"
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
		CREATE TABLE IF NOT EXISTS my_order(
    		id INTEGER PRIMARY KEY ,
    		EMAIL TEXT NOT NULL ,
    		STATUS TEXT NOT NULL,
		    SUMM INTEGER NOT NULL ,
		    ORDER_TEXT TEXT NOT NULL );
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

func (s *Storage) SaveOrder(email string, status string, sum int, orderText string) error {
	const op = "storage.sqlite.SaveUser"

	stmt, err := s.db.Prepare("INSERT INTO my_order(email, status,summ,order_text) VALUES(?, ?, ?, ?)")
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	_, err = stmt.Exec(email, status, sum, orderText)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}
	return nil
}

func (s *Storage) GetOrdersByEmail(email string) ([]Order, error) {
	const op = "storage.sqlite.GetTicketsByEmail"

	stmt, err := s.db.Prepare("SELECT id, status,summ, order_text FROM my_order WHERE email = ?")
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}
	defer stmt.Close()

	rows, err := stmt.Query(email)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}
	defer rows.Close()

	var orders []Order
	for rows.Next() {
		var order Order
		if err := rows.Scan(&order.Id, &order.Status, &order.Sum, &order.OrdersText); err != nil {
			return nil, fmt.Errorf("%s: %w", op, err)
		}
		orders = append(orders, order)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	if len(orders) == 0 {
		return nil, storage.ErrOrdersNotFound
	}

	return orders, nil
}

type Order struct {
	Id         int    `json:"id"`
	Sum        int    `json:"sum"`
	Status     string `json:"status"`
	OrdersText string `json:"orders_text"`
}
