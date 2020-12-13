## Zippy

* A utility that compresses files and folders into a set of compressed files such that each compressed file doesn't
  exceed a maximum size.
* It can be used for decompressing the files that it has generated earlier.
* All the files in a directory can be compressed/ decompressed in a multi-threaded fashion.
* For all this it currently uses JDK’s implementation of ***[Zip](https://en.wikipedia.org/wiki/ZIP_(file_format))***
  compression algorithm found in *[
  java.util.zip](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/zip/package-summary.html)*
  and can be easiliy extended to support other algorithms.
  <br>
* This is written in Java 11 and build using Maven.

### Implementation

* **Zip**

    1. Contents of an input file is read using file input stream into a buffer of size of 1KB, this supports the
       requirement of being able to compress files which are greater than the JVM heap size.
    2. To support the maximum size requirement on the zipped output, content read from the input stream is written to a
       sequence of ***partFiles***.
    3. As the total chunk of bytes written already to a partFile become greater than the maximum size desired, a new
       partFile is created with auto-incrementing part number, the next chunk of bytes is written the new partFile and
       so on.
    4. The final outcome is a set of .zip files.
       <br><br>

* **Un-Zip**
    1. The sequence of .zip files created during the compression process are read in a sorted ascending order based on
       the part number.
    2. Content from each partFile is read in a buffered manner and written to an output file.
    3. The final outcome is a file same as the original input file before compression.

## Getting Started

### Prerequisites

If you've not got Maven or Java installed already, please install those or alternatively you can run the application
using Docker.

### Setup and Usage

Either of the following methods can be used to setup the application as desired.

#### 1. Building and Running a JAR

* Build the JAR file using maven, the output - *zippy.jar* would be in /target directory in the project folder.
  ```sh
  $mvn clean package  
   ```

* Run the application with desired args.
    1. To Compress -

  ```sh
  $java -jar target/zippy.jar -c path_input_dir path_out_dir max_size
   ```
    2. To Decompress -

  ```sh
  $java -jar target/zippy.jar -d path_input_dir path_out_dir
   ```

#### 2. Using Docker

The idea is to run the application leveraging docker volumes for mapping host directories to be zipped and unzipped.

* Build the docker image from project directory
    ```sh
  $docker build -t zippy .
   ```
  The build might take some time as it would download the image and maven dependencies. Verify that the build was
  successful and proceed.
  <br><br>
* Run the application with desired args.
    1. To Compress -
    ```sh
    $docker run -v path_input_dir:/mnt/source -v path_out_dir:/mnt/dest zippy java -jar /app/target/zippy.jar -c /mnt/source /mnt/dest max_size   
    ``` 
    2. To Decompress -
    ```sh
    $docker run -v path_input_dir:/mnt/dest -v path_out_dir:/mnt/out zippy java -jar /app/target/zippy.jar -d /mnt/dest /mnt/out   
    ```
  Only thing to ensure is that source /mnt/dir for the container during decompression is same as the destination during
  compression.

***NOTE*** :

1. By default the application runs with a single thread, that can be changed by updating the *ThreadCount*
   property in *config.properties* file.
2. Before running zip with a given destination directory, please make sure there are no old .zip files present as that
   can cause issue during unzip because the files of zip step are not cleared implicitly during unzip.