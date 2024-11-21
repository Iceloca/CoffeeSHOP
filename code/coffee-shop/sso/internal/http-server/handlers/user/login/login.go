package login

import (
	"errors"
	"github.com/go-playground/validator/v10"
	"golang.org/x/crypto/bcrypt"
	"io"
	"net/http"

	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"log/slog"

	resp "sso/internal/lib/api/response"
	"sso/internal/lib/logger/sl"
	"sso/internal/storage"
)

type Request struct {
	Email    string `json:"email" validate:"required,email"`
	Password string `json:"password" validate:"required,min=8"`
}

type Response struct {
	resp.Response
}

//go:generate go run github.com/vektra/mockery/v2@v2.46.3	 --name=URLSaver
type PassHashGetter interface {
	GetPassHash(email string) ([]byte, error)
}

func New(log *slog.Logger, passHashGetter PassHashGetter) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.user.add_ticket.New"

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

		log.Info("logging in user")

		passHashFromBD, err := passHashGetter.GetPassHash(req.Email)

		if err != nil {
			if errors.Is(err, storage.ErrUserNotFound) {
				log.Error("user doesn't exist", slog.String("email", req.Email))

				w.WriteHeader(http.StatusInternalServerError)

				render.JSON(w, r, resp.Error("internal server error"))

				return
			}
			log.Error("failed to get hash", sl.Err(err))

			w.WriteHeader(http.StatusInternalServerError)

			render.JSON(w, r, resp.Error("failed to register user"))

			return
		}
		if bcrypt.CompareHashAndPassword(passHashFromBD, []byte(req.Password)) != nil {
			log.Error("wrong password", slog.String("email", req.Email))

			w.WriteHeader(http.StatusInternalServerError)

			render.JSON(w, r, resp.Error("internal server error"))

			return
		}
		log.Info("user register", slog.String("email", req.Email))

		responseOK(w, r)
	}
}

func responseOK(w http.ResponseWriter, r *http.Request) {
	render.JSON(w, r, Response{
		Response: resp.OK(),
	})
}
