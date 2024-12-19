import React, { useState, useEffect } from 'react';
import { getAuth, onAuthStateChanged } from 'firebase/auth';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, TextField, Button } from '@mui/material';
import * as pdfjsLib from 'pdfjs-dist';
import { getDocument, GlobalWorkerOptions } from 'pdfjs-dist';
import * as mammoth from 'mammoth';

// Set the workerSrc if needed (use a CDN or a local file path)
GlobalWorkerOptions.workerSrc = `https://cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjsLib.version}/pdf.worker.min.js`;

const ResumeUpload = () => {
  const [user, setUser] = useState(null);
  const [fileText, setFileText] = useState('');
  const [file, setFile] = useState(null);
  const navigate = useNavigate();
  const auth = getAuth();
  

  // Check if the user is logged in
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      if (currentUser) {
        setUser(currentUser);
      } else {
        navigate('/signup');
      }
    });

    return () => unsubscribe();
  }, [auth, navigate]);

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (file) {
      const fileType = file.type;
      if (fileType === 'application/pdf') {
        const text = await extractTextFromPdf(file);
        setFileText(text);
      } else if (fileType === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
        const text = await extractTextFromDocx(file);
        setFileText(text);
      } else if (fileType === 'text/plain') {
        const text = await extractTextFromTxt(file);
        setFileText(text);
      } else {
        alert('Please upload a PDF, DOCX, or TXT file');
      }
    }
  };

  const extractTextFromDocx = async (file) => {
    const arrayBuffer = await file.arrayBuffer();
    const result = await mammoth.extractRawText({ arrayBuffer });
    return result.value;
  };

  const extractTextFromTxt = async (file) => {
    const fileReader = new FileReader();
    return new Promise((resolve, reject) => {
      fileReader.onload = (e) => {
        const text = e.target.result;
        resolve(text);
      };
      fileReader.onerror = (error) => reject(error);
      fileReader.readAsText(file); // Read the text file content
    });
  };

  const extractTextFromPdf = async (file) => {
    const fileReader = new FileReader();
    return new Promise((resolve, reject) => {
      fileReader.onload = async (e) => {
        const pdfData = new Uint8Array(e.target.result);
        try {
          const pdf = await getDocument(pdfData).promise;
          const numPages = pdf.numPages;
          let text = '';

          for (let pageNum = 1; pageNum <= numPages; pageNum++) {
            const page = await pdf.getPage(pageNum);
            const content = await page.getTextContent();
            const pageText = content.items.map(item => item.str).join(' ');
            text += pageText + '\n';
          }

          resolve(text);
        } catch (error) {
          reject(error);
        }
      };
      fileReader.onerror = (error) => reject(error);
      fileReader.readAsArrayBuffer(file);
    });
  };

  return (
    <>
    <Box sx={{ padding: 2 }}>
      <h2>Upload Resume</h2>
      <input
        type="file"
        accept=".pdf,.docx,.txt"
        onChange={handleFileUpload}
      />
      <TextField
        label="Extracted Text"
        multiline
        rows={10}
        variant="outlined"
        value={fileText}
        fullWidth
        sx={{ marginTop: 2 }}
      />
    </Box>
    </>
  );
};

export default ResumeUpload;