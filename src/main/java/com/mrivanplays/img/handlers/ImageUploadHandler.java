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

import com.mrivanplays.img.utils.RandomStringGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import spark.Request;
import spark.Response;
import spark.Route;

public class ImageUploadHandler implements Route {

  private String imagesDirectoryPath;
  private MultipartConfigElement config;
  private String authenticationToken;

  public ImageUploadHandler(File baseDirectory, File imagesDirectory) {
    this.imagesDirectoryPath = imagesDirectory.toPath().toString();
    config = new MultipartConfigElement(imagesDirectoryPath, 100000000, 100000000, 1024);
    try (BufferedReader reader =
        new BufferedReader(new FileReader(new File(baseDirectory, "authentication.txt")))) {
      authenticationToken = reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String authenticationTokenGiven = request.headers("auth-key");
    if (!authenticationTokenGiven.equalsIgnoreCase(authenticationToken)) {
      response.type("text");
      response.status(403);

      return "Invalid authentication token. You're not allowed to upload images here!";
    }
    request.raw().setAttribute("org.eclipse.jetty.multipartConfig", config);
    Part filePart = request.raw().getPart("image");
    String extension = filePart.getSubmittedFileName().split("\\.")[1];
    String fileName = RandomStringGenerator.generateRandomString() + "." + extension;
    Path out = Paths.get(imagesDirectoryPath + File.separator + fileName);
    try (InputStream in = filePart.getInputStream()) {
      Files.copy(in, out);
      filePart.delete();
    }
    return "https://img.mrivanplays.com/" + fileName;
  }
}
