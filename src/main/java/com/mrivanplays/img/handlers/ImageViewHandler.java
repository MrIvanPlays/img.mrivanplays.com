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
package com.mrivanplays.img.handlers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import spark.Request;
import spark.Response;
import spark.Route;

public class ImageViewHandler implements Route {

  private File imagesDirectory;

  public ImageViewHandler(File imagesDirectory) {
    this.imagesDirectory = imagesDirectory;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String id = request.params(":imageLink");
    File[] files = imagesDirectory.listFiles((dir, name) -> {
      if (id.indexOf('.') != -1) {
        return name.equalsIgnoreCase(id);
      }
      String actualName = name.replace(".", ",").split(",")[0];
      return actualName.equalsIgnoreCase(id);
    });
    File responseFile = null;
    if (files == null) {
      responseFile = new File("/usr/share/nginx/imgserver/404.png");
    }
    if (responseFile == null && files.length != 1) {
      responseFile = new File("/usr/share/nginx/imgserver/404.png");
    }
    if (responseFile == null) {
      responseFile = files[0];
    }
    String fileExtension = responseFile.getName().replace(".", ",").split(",")[1];
    response.raw().setContentType("image/" + fileExtension);
    response.raw().setContentLengthLong(responseFile.length());
    response.status(200);

    try (InputStream in = new BufferedInputStream(new FileInputStream(responseFile))) {
      try (OutputStream out = new BufferedOutputStream(response.raw().getOutputStream())) {
        byte[] buf = new byte[8192];
        while (true) {
          int r = in.read(buf);
          if (r == -1) {
            break;
          }
          out.write(buf, 0, r);
        }
      }
    }
    return "";
  }
}
