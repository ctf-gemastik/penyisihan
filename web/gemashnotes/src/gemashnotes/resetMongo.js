const mongoose = require("mongoose");
const Note = require('./models/note');

const resetMongo = async() => {
    await mongoose.connect(
        process.env.MONGO_URL ||
        "mongodb://localhost:27017/mydata"
    );
    await Note.deleteMany({});
    console.log("Done reset");
    process.exit();
}

resetMongo();