// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getAuth, GoogleAuthProvider } from 'firebase/auth';

// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyBcrfYBz76rsBh8xN6IbrRBX3Ee1bvYdr8",
  authDomain: "resumegenie-5562d.firebaseapp.com",
  projectId: "resumegenie-5562d",
  storageBucket: "resumegenie-5562d.firebasestorage.app",
  messagingSenderId: "1052290385168",
  appId: "1:1052290385168:web:a45f19a3e4d07330585195",
  measurementId: "G-JLSHWTC2B9"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Get auth instance
const auth = getAuth(app);
const googleProvider = new GoogleAuthProvider();

export { auth, googleProvider };
export default firebaseConfig ;
