package save_order

import (
	"errors"
	"github.com/go-playground/validator/v10"
	"io"
	"net/http"

	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"log/slog"

	resp "orders/internal/lib/api/response"
	"orders/internal/lib/logger/sl"
)

type Request struct {
	Email      string `json:"email" validate:"required,email"`
	Sum        int    `json:"sum"  validate:"required"`
	Status     string `json:"status" validate:"required"`
	OrdersText string `json:"orders_text" validate:"required"`
}

type Response struct {
	resp.Response
}

//go:generate go run github.com/vektra/mockery/v2@v2.46.3	 --name=URLSaver
type OrderSaver interface {
	SaveOrder(email string, status string, sum int, orderText string) error
}

func New(log *slog.Logger, orderSaver OrderSaver) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.user.save_order.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		var req Request

		err := render.DecodeJSON(r.Body, &req)

		if err != nil {
			if errors.Is(err, io.EOF) {
				log.Error("request body is empty")

				w.WriteHeader(http.StatusBadRequest)

				render.JSON(w, r, resp.Error("empty request"))

				return
			}

			log.Error("failed to decode request body", sl.Err(err))

			w.WriteHeader(http.StatusBadRequest)

			render.JSON(w, r, resp.Error("failed to decode request"))

			return
		}

		log.Info("request body decoded", slog.Any("request", req))

		if err := validator.New().Struct(req); err != nil {
			validateErr := err.(validator.ValidationErrors)

			log.Error("invalid request", sl.Err(err))

			w.WriteHeader(http.StatusBadRequest)

			render.JSON(w, r, resp.ValidationError(validateErr))

			return
		}

		log.Info("saving order")

		err = orderSaver.SaveOrder(req.Email, req.Status, req.Sum, req.OrdersText)

		if err != nil {

			log.Error("failed to save order", sl.Err(err))

			w.WriteHeader(http.StatusInternalServerError)

			render.JSON(w, r, resp.Error("failed to save order"))

			return
		}

		responseOK(w, r)
	}
}

func responseOK(w http.ResponseWriter, r *http.Request) {
	render.JSON(w, r, Response{
		Response: resp.OK(),
	})
}
