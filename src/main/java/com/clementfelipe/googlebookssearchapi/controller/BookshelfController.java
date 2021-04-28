package com.clementfelipe.googlebookssearchapi.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.clementfelipe.googlebookssearchapi.gateway.GoogleBooksGateway;
import com.google.api.services.books.v1.model.Bookshelf;
import com.google.api.services.books.v1.model.Volume;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public class BookshelfController {
  
  @Autowired
  GoogleBooksGateway googleBooksGateway;

  @GetMapping("/bookshelf")
  public List<BookshelfDTO> setFavorite(
    @RequestHeader("authorization") String userToken
  ) throws GeneralSecurityException, IOException {

    List<Bookshelf> bookshelves = googleBooksGateway.getUserBookshelves(userToken);

    return bookshelves.stream().map(b -> BookshelfDTO.fromClientBookshelf(b)).collect(Collectors.toList());
  }

  @GetMapping("/bookshelf/{bookshelfId}/volumes")
  public List<VolumeDTO> setFavorite(
    @PathVariable String bookshelfId,
    @RequestHeader("authorization") String userToken
  ) throws GeneralSecurityException, IOException {

    List<Volume> volumes = googleBooksGateway.getUserBookshelfVolumes(bookshelfId, userToken);

    return volumes.stream().map(v -> VolumeDTO.fromClientVolume(v, new HashSet<>())).collect(Collectors.toList());
  }
}
