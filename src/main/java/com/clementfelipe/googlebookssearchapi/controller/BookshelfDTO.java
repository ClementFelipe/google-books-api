package com.clementfelipe.googlebookssearchapi.controller;

import com.google.api.services.books.v1.model.Bookshelf;

public class BookshelfDTO {
  private Integer id;
  private String title;

  private BookshelfDTO() {}

  public static BookshelfDTO fromClientBookshelf(Bookshelf bookshelf) {
    BookshelfDTO bookshelfDTO = new BookshelfDTO();

    bookshelfDTO.id = bookshelf.getId();
    bookshelfDTO.title = bookshelf.getTitle();

    return bookshelfDTO;
  }

  public Integer getId() {
    return this.id;
  }

  public String getTitle() {
    return this.title;
  }
}
