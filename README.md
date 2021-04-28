# Google Books API Client

This is a wrapper API for some of the functionality of the Google Books API. This API allows the following:

For unauthenticated users:

- Search public volumes via query and scope (ISBN, Magazines, or All)

For authenticated users (clients must use a valid authorization token granted via OAuth2 with Google):

- Search will show which volumes belong to a users `Favorites` bookshelf
- List a users bookshelves
- See the volumes in a specific user bookshelf
- Add/remove volumes from a users `Favorites` bookshelf

## API

There are two resources in this API: `bookshelf` and `volume`,

### bookshelf

This resource has the following structure:

```js
{
  id: number, // numeric id given by Google Books API
  title: string // title (name) of the bookshelf
}
```

Endpoints are:

> GET /bookshelf

Obtains all the users bookshelves.

> GET /bookshelf/{bookshelfId}/volume

Obtains the volumes in a given bookshelf for the user.

### volume

This resource has the following structure:

```js
{
  id: string, // alphanumeric id given by Google Books API
  title: string, // title of the volume
  imageURL: string, // URL for the volumes preview image
  authors: [string], // list of volume author names
  categories: [string], // list of volume categories
  publishedDate: string, // year of publication
  isUserFavorite: boolean // indicates if the volume is present in the users "Favorites" bookshelf
}
```

Endpoints are:

> GET /volume

Obtain a list of volumes via query and scope. If no scope is specified then the list of volumes is searched across all volumes. When this endpoint is called with an authenticated Google users token, the `isUserFavorite` field in the volume will indicate if the volume belongs to the users `Favorites` bookshelf, otherwise all volumes will have the value as `false`.

`scope` may be used with values:

* isbn: Will cause the endpoint to interpret the query string as an ISBN number
* magazines: Will query the volumes belonging to the `magazines` print type

Any other value for `scope` will be interpreted as a search across all volumes.

> PUT /volume/{volumeId}/favorite/{isFavorite}

Depending on the value of `isFavorite` (true|false), this endpoint may either add the volume to the users `Favorites` bookshelf or remove it.

This endpoint does not return a response body.
