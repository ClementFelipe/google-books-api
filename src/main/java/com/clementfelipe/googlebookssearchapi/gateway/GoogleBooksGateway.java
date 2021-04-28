package com.clementfelipe.googlebookssearchapi.gateway;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.v1.Books;
import com.google.api.services.books.v1.BooksRequestInitializer;
import com.google.api.services.books.v1.Books.Mylibrary.Bookshelves.AddVolume;
import com.google.api.services.books.v1.Books.Mylibrary.Bookshelves.RemoveVolume;
import com.google.api.services.books.v1.Books.Volumes.List;
import com.google.api.services.books.v1.model.Bookshelf;
import com.google.api.services.books.v1.model.Bookshelves;
import com.google.api.services.books.v1.model.Volume;
import com.google.api.services.books.v1.model.Volumes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.stereotype.Service;

@Service
public class GoogleBooksGateway {
  public java.util.List<Volume> getVolumesByQuery(String query, String printType) throws IOException, GeneralSecurityException {
    final Books booksClient = new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), null)
      .setApplicationName(null)
      .setGoogleClientRequestInitializer(new BooksRequestInitializer("AIzaSyBdGr8xl4kR_hO6_dzDNwv5fTvePn6ppRg"))
      .build();

    Volumes volumes = booksClient
      .volumes()
      .list(query)
      .setPrintType(printType != null ? printType : "all")
      .execute();

    return volumes.getItems() != null ? volumes.getItems() : new ArrayList<>();
  }

  public void addVolumeToBookshelf(String volumeId, String bookshelfId, String userToken) throws IOException, GeneralSecurityException {
    getAuthenticatedUserClient(userToken)
      .mylibrary()
      .bookshelves()
      .addVolume(bookshelfId, volumeId)
      .execute();
  }

  public void removeVolumeFromBookshelf(String volumeId, String bookshelfId, String userToken) throws IOException, GeneralSecurityException {
    getAuthenticatedUserClient(userToken)
      .mylibrary()
      .bookshelves()
      .removeVolume(bookshelfId, volumeId)
      .execute();
  }

  public java.util.List<Volume> getUserBookshelfVolumes(String bookshelfId, String userToken) throws IOException, GeneralSecurityException {
    Volumes volumes = getAuthenticatedUserClient(userToken)
      .mylibrary()
      .bookshelves()
      .volumes()
      .list(bookshelfId)
      .execute();

    return volumes.getItems() != null ? volumes.getItems() : new ArrayList<>();
  }

  public java.util.List<Bookshelf> getUserBookshelves(String userToken) throws IOException, GeneralSecurityException {
    Bookshelves bookshelves = getAuthenticatedUserClient(userToken)
      .mylibrary()
      .bookshelves()
      .list()
      .execute();

    return bookshelves.getItems() != null ? bookshelves.getItems() : new ArrayList<>();
  }

  private Books getAuthenticatedUserClient(String userToken) throws GeneralSecurityException, IOException {
    GoogleCredentials credentials = GoogleCredentials.newBuilder().setAccessToken(new AccessToken(userToken, null)).build();

    return new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
      .setApplicationName(null)
      .build();
  }
}
