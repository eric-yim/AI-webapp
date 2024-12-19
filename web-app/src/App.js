import React, { useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { setPersistence, browserLocalPersistence } from 'firebase/auth';
import { auth } from './firebaseConfig';
import LandingPage from './LandingPage';
import LoginPage from './LoginPage';
import ResumeUpload from './ResumeUpload';
import PaymentsPage from './PaymentsPaypal';
import Header from './Header';
import { UserProvider } from './UserContext';
import Footer from './Footer';
import PrivacyPolicyPage from './PrivacyPolicyPage';
import TermsOfServicePage from './TermsOfServicePage';
import Profile from './Profile';
import { CssBaseline, Box } from "@mui/material";

const App = () => {

  useEffect(() => {

    // Set the persistence to local storage
    setPersistence(auth, browserLocalPersistence)
      .then(() => {
        // Persistence is set successfully
        console.log('Firebase session persistence set to localStorage');
      })
      .catch((error) => {
        // Handle error in setting persistence
        console.error('Error setting persistence', error);
      });
  }, []);


  return (
    <UserProvider>
      
        <CssBaseline />
      <Router>
        <Header />
        <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          minHeight: "80vh",
        }}
        >
        
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/signup" element={<LoginPage />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/resume-tool" element={<ResumeUpload />} />
          <Route path="/payment" element={<PaymentsPage />} />
          <Route path="/privacy-policy" element={<PrivacyPolicyPage />} />
          <Route path="/terms-of-service" element={<TermsOfServicePage />} />
          {/* Redirect unknown routes */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
        </Box>
        <Footer />
      </Router>
 
    </UserProvider>
  );
};

export default App;