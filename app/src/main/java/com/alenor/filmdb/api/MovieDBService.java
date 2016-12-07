package com.alenor.filmdb.api;

import com.alenor.filmdb.model.AccountInfo;
import com.alenor.filmdb.model.ChangeFavoritesBody;
import com.alenor.filmdb.model.ChangeFavoritesResponse;
import com.alenor.filmdb.model.ChangePlaylistBody;
import com.alenor.filmdb.model.ChangeWatchlistBody;
import com.alenor.filmdb.model.ChangeWatchlistResponse;
import com.alenor.filmdb.model.CreatePlaylistBody;
import com.alenor.filmdb.model.CreatePlaylistResponse;
import com.alenor.filmdb.model.DeletePlaylistResponse;
import com.alenor.filmdb.model.GenreContainer;
import com.alenor.filmdb.model.Movie;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.model.MovieImagesContainer;
import com.alenor.filmdb.model.PlaylistContainer;
import com.alenor.filmdb.model.PlaylistItemsContainer;
import com.alenor.filmdb.model.Session;
import com.alenor.filmdb.model.StatusResponse;
import com.alenor.filmdb.model.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface MovieDBService {

    String MOVIE_DB_URL = "https://www.themoviedb.org/movie/";
    String BASE_URL = "http://api.themoviedb.org/3/";
    String IMAGE_780W_BASE_URL = "https://image.tmdb.org/t/p/w780";


    @GET("genre/movie/list")
    Observable<GenreContainer> getGenresList();

    @GET("movie/{id}")
    Call<Movie> getMovieById(@Path("id") long id);

    @GET("genre/{id}/movies")
    Observable<MovieContainer> getMovieByGenreId(@Path("id") long id, @Query("page") Integer page);

    @GET("movie/{id}/images")
    Call<MovieImagesContainer> getMovieImagesByMovieId(@Path("id") long id);

    @GET("movie/top_rated")
    Call<MovieContainer> getTopRated();

    @GET("search/movie")
    Call<MovieContainer> searchMovieByTitle(@Query("query") String movieTitle,
                                            @Query("include_adult") boolean includeAdult);

    @GET("authentication/token/new")
    Observable<Token> getToken();

    @GET("authentication/token/validate_with_login")
    Observable<Token> validateWithLogin(@Query("request_token") String token,
                                  @Query("username") String username, @Query("password") String password);

    @GET("authentication/session/new")
    Observable<Session> createNewSession(@Query("request_token") String token);

    @GET("account")
    Call<AccountInfo> getAccountInfo(@Query("session_id") String sessionId);

    @GET("account/{id}/favorite/movies")
    Call<MovieContainer> getFavoriteMovies(@Path("id") long accountId, @Query("session_id") String sessionId);

    @POST("account/{id}/favorite")
    Call<ChangeFavoritesResponse> changeFavorites(@Query("session_id") String sessionId, @Body ChangeFavoritesBody body);

    @GET("account/{id}/watchlist/movies")
    Call<MovieContainer> getWatchlist(@Path("id") long accountId, @Query("session_id") String sessionId);

    @POST("account/{id}/watchlist")
    Call<ChangeWatchlistResponse> changeWatchlist(@Query("session_id") String sessionId, @Body ChangeWatchlistBody body);

    @GET("account/{id}/lists")
    Call<PlaylistContainer> getPlaylists(@Path("id") long accountId, @Query("session_id") String session_id);

    @POST("list")
    Call<CreatePlaylistResponse> createPlaylist(@Query("session_id") String sessionId, @Body CreatePlaylistBody body);

    @DELETE("list/{id}")
    Call<DeletePlaylistResponse> deletePlaylist(@Path("id") String playlistId, @Query("session_id") String sessionId);

    @GET("list/{id}")
    Call<PlaylistItemsContainer> getPlaylist(@Path("id") String playlistId);

    @POST("list/{id}/add_item")
    Call<StatusResponse> addMovieToPlaylist(@Path("id") String playlistId, @Query("session_id") String sessionId,
                                            @Body ChangePlaylistBody body);

    @POST("list/{id}/remove_item")
    Call<StatusResponse> removeMovieFromPlaylist(@Path("id") String playlistId, @Query("session_id") String sessionId,
                                                 @Body ChangePlaylistBody body);

}
