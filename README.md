# IF5010-2018-HW1
Buat tugas 1 Arsikip

## Cara compile
```
# Pastikan membuat folder build terlebih dahulu
# Ordinary command
javac -d build src/components/*.java src/*.java

# Linux terminal
javac -d build $(find . -name "*.java")
```

## Cara membuat file jar
```
# Pastikan membuat folder dist terlebih dahulu
jar cmvf META-INF/MANIFEST.MF dist/app.jar -C build .
```

# Cara menjalankan file jar
```
java -jar dist/app.jar
```
