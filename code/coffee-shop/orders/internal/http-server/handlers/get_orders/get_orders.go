package get_orders

import (
	"errors"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"log/slog"
	"net/http"
	resp "orders/internal/lib/api/response"
	"orders/internal/lib/logger/sl"
	"orders/internal/storage"
	"orders/internal/storage/sqlite"
)

type Request struct {
	Email string `json:"email,omitempty"`
}

//go:generate go run github.com/vektra/mockery/v2@v2.46.3	 --name=URLGetter
type OrdersGetter interface {
	GetOrdersByEmail(email string) ([]sqlite.Order, error)
}

func New(log *slog.Logger, ordersGetter OrdersGetter) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.orders.get.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		var req Request

		email := r.URL.Query().Get("email")

		if email == "" {
			log.Error("missing email parameter")
			render.JSON(w, r, resp.Error("email parameter is required"))
			return
		}
		req.Email = email

		log.Info("request param decoded", slog.Any("email", req.Email))

		orders, err := ordersGetter.GetOrdersByEmail(req.Email)

		if err != nil {
			if !errors.Is(err, storage.ErrOrdersNotFound) {
				log.Error("failed to redirect url", sl.Err(err))

				render.JSON(w, r, resp.Error("failed to redirect url"))

				return
			}
			log.Info("no orders found", slog.String("email", req.Email))
			render.JSON(w, r, resp.OK())
			return
		}

		// Return the orders in the response
		log.Info("orders retrieved", slog.String("email", req.Email))
		render.JSON(w, r, struct {
			resp.Response
			Orders []sqlite.Order `json:"orders"`
		}{
			Response: resp.OK(),
			Orders:   orders,
		})
	}
}
