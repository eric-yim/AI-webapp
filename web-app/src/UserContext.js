import React, { createContext, useState, useEffect } from 'react';
import { onAuthStateChanged } from "firebase/auth";
import { auth } from './firebaseConfig'; 

export const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [credits, setCredits] = useState(0);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
      if (firebaseUser) {
        const token = await firebaseUser.getIdToken();
        
        try {
            const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/users`, {
              method: 'GET',
              headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`,
              },
            });
      
            if (response.ok) {
              const data = await response.json();
              console.log('User data found:', data);
              setCredits(data.credits);
            } else {
              const errorDetails = await response.json(); // Attempt to read error details from the response
              console.error('Error in backend:', errorDetails);
            }
          } catch (error) {
            console.error('Error sending user data to backend:', error);
        }
        setUser(firebaseUser);
        
        
      } else {
        setUser(null);
        setCredits(0);
      }
    });

    return () => unsubscribe();
  }, []);
  return (
    <UserContext.Provider value={{ user, credits, setCredits }}>
      {children}
    </UserContext.Provider>
  );
};