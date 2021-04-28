package com.clementfelipe.googlebookssearchapi.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.clementfelipe.googlebookssearchapi.gateway.GoogleBooksGateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class VolumeController {
  
  @Autowired
  GoogleBooksGateway googleBooksGateway;

  @GetMapping("/volume")
  public List<VolumeDTO> get(
    @RequestParam String query,
    @RequestParam(required = false, defaultValue = "") String scope,
    @RequestHeader(name = "authorization", required = false) String userToken
  ) throws GeneralSecurityException, IOException {

    final Set<String> userFavoriteVolumes = userToken == null
      ? new HashSet<>()
      : googleBooksGateway.getUserBookshelfVolumes("0", TokenUtils.removeTokenPrefix(userToken)).stream()
          .map(v -> v.getId()).collect(Collectors.toSet());

    String printType = scope.equals("magazines") ? "magazines" : "all";
    String effectiveQuery = scope.equals("isbn") ? "isbn:" + query : query;      

    return googleBooksGateway.getVolumesByQuery(effectiveQuery, printType).stream()
      .map(v -> VolumeDTO.fromClientVolume(v, userFavoriteVolumes))
      .collect(Collectors.toList());
  }

  @PutMapping("/volume/{volumeId}/favorite/{isFavorite}")
  public void setFavorite(
    @PathVariable String volumeId,
    @PathVariable Boolean isFavorite,
    @RequestHeader(name = "authorization", required = true) String userToken
  ) throws GeneralSecurityException, IOException {

    String parsedToken = TokenUtils.removeTokenPrefix(userToken);

    if (isFavorite) {
      googleBooksGateway.addVolumeToBookshelf(volumeId, "0", parsedToken);
    } else {
      googleBooksGateway.removeVolumeFromBookshelf(volumeId, "0", parsedToken);
    }
  }

}
