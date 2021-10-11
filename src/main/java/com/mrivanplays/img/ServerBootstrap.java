/*
    Copyright (c) 2019 Ivan Pekov
    Copyright (c) 2019 Contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/
package com.mrivanplays.img;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.port;

import com.mrivanplays.img.handlers.FaviconHandler;
import com.mrivanplays.img.handlers.ImageUploadHandler;
import com.mrivanplays.img.handlers.ImageViewHandler;
import java.io.File;

public class ServerBootstrap {

  public static void main(String[] args) {
    port(9899);
    initExceptionHandler(Throwable::printStackTrace);

    File baseDirectory = new File("/usr/share/nginx/imgserver/");
    if (!baseDirectory.exists()) {
      baseDirectory.mkdirs();
    }

    File imagesDirectory = new File(baseDirectory, "images/");
    if (!imagesDirectory.exists()) {
      imagesDirectory.mkdirs();
    }

    FaviconHandler favicon = new FaviconHandler();
    get("/favicon.ico", favicon);

    ImageViewHandler imgView = new ImageViewHandler(imagesDirectory);
    get("/:imageLink", imgView);
    get("/:imageLink/", imgView);

    ImageUploadHandler uh = new ImageUploadHandler(baseDirectory, imagesDirectory);
    post("/upload/", "multipart/form-data", uh);

    get(
        "/",
        (request, response) -> {
          response.type("text/html");
          response.status(200);

          return "<!doctype html><html><head><title>MrIvanPlays Image Server</title></head><h1>Nothing to see here. Use https://img.mrivanplays.com/[imgname] to view images.</h1></html>";
        });
  }
}
