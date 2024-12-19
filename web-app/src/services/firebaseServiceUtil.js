// src/services/firebaseService.js

import { getFirestore, doc, setDoc, getDoc, updateDoc } from 'firebase/firestore';
import { getAuth } from 'firebase/auth';

const db = getFirestore();
const auth = getAuth();

// Get or create user data
export const getUserData = async (userId) => {
  const userRef = doc(db, 'users', userId);
  const docSnap = await getDoc(userRef);
  
  if (docSnap.exists()) {
    // User exists, return their data
    return docSnap.data();
  } else {
    // User does not exist, create a new document with initial credits
    await setDoc(userRef, {
      credits: 0,  // Start with 0 credits
    });
    return { credits: 0 };  // Default user data
  }
};

// Deduct credits after resume generation
export const deductCredits = async (userId) => {
  const userRef = doc(db, 'users', userId);
  const userData = await getUserData(userId);
  
  if (userData.credits > 0) {
    // If the user has credits, deduct 1 credit
    await updateDoc(userRef, {
      credits: userData.credits - 1,
    });
  } else {
    alert('You do not have enough credits to generate a resume. Please purchase more.');
  }
};

// Add credits after payment (e.g., after Stripe payment)
export const addCredits = async (userId, amount) => {
  const userRef = doc(db, 'users', userId);
  const userData = await getUserData(userId);
  
  await updateDoc(userRef, {
    credits: userData.credits + amount,
  });
};