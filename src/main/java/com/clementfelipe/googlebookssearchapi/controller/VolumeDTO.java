package com.clementfelipe.googlebookssearchapi.controller;

import java.util.List;
import java.util.Set;

import com.google.api.services.books.v1.model.Volume;

public class VolumeDTO {
  private String id;
  private String title;
  private String imageURL;
  private List<String> authors;
  private List<String> categories;
  private String publishedDate;
  private boolean isUserFavorite;

  private VolumeDTO() {}

  static VolumeDTO fromClientVolume(final Volume volume, final Set<String> favoriteVolumesIds) {
    final VolumeDTO volumeDTO = new VolumeDTO();

    volumeDTO.id = volume.getId();
    volumeDTO.title = volume.getVolumeInfo().getTitle();
    volumeDTO.imageURL = volume.getVolumeInfo().getPreviewLink();
    volumeDTO.authors = volume.getVolumeInfo().getAuthors();
    volumeDTO.categories = volume.getVolumeInfo().getCategories();
    volumeDTO.publishedDate = volume.getVolumeInfo().getPublishedDate();
    volumeDTO.isUserFavorite = favoriteVolumesIds.contains(volume.getId());

    return volumeDTO;
  }

  public String getid() {
    return this.id;
  }

  public String getTitle() {
    return this.title;
  }

  public String getImageURL() {
    return this.imageURL;
  }

  public List<String> getAuthors() {
    return this.authors;
  }

  public List<String> getCategories() {
    return this.categories;
  }

  public String getPublishedDate() {
    return this.publishedDate;
  }

  public boolean getIsUserFavorite() {
    return this.isUserFavorite;
  }

  public void setIsUserFavorite(boolean isUserFavorite) {
    this.isUserFavorite = isUserFavorite;
  }

}
