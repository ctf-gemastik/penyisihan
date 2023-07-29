var express = require('express');
var router = express.Router();
const Note = require('../models/note');

router.get('/', (req, res, next) => {
  return res.status(200).json({status: "ok"});
});

router.get('/stats', async(req, res, next) => {
  const allNotes = await Note.find();
  return res.status(200).json({count: allNotes.length});
});

router.post('/notes', async(req, res, next) => {
  const newNote = new Note({ ...req.body });
  const insertedNote = await newNote.save();
  return res.status(201).json(insertedNote);
});

router.get('/notes/:id', async(req, res, next) => {
  try {
    const { id } = req.params;
    const note = await Note.findById(id).exec();
    if(!note) return res.status(404).send();
    return res.status(200).json(note);
  } catch (e) {
    return res.status(400).send();
  }
});

router.put('/notes/:id', async(req, res, next) => {
  try {
    const { id } = req.params;
    const note = await Note.findByIdAndUpdate(id, req.body, { new: true });
    if(!note) return res.status(404).send();
    return res.status(200).json(note);
  } catch (e) {
    return res.status(400).send();
  }
});

module.exports = router;
