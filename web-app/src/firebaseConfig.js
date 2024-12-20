// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getAuth, GoogleAuthProvider } from 'firebase/auth';

// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
//////////////////SEE README
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Get auth instance
const auth = getAuth(app);
const googleProvider = new GoogleAuthProvider();

export { auth, googleProvider };
export default firebaseConfig ;
