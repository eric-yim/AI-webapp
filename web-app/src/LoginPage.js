import React, { useContext } from 'react';
import { Button, Box, Typography } from '@mui/material';
import { auth, googleProvider } from './firebaseConfig';
import { signInWithPopup } from 'firebase/auth';
import { useNavigate } from 'react-router-dom';
import { UserContext } from './UserContext';

const LoginPage = () => {
  const navigate = useNavigate();

  const { user } = useContext(UserContext);

  const handleGoogleSignIn = async () => {
    try {
      const result = await signInWithPopup(auth, googleProvider);
      const signedInUser = result.user;
      console.log('User signed in:', signedInUser);
      //await getUserData(signedInUser);
      alert(`Welcome ${signedInUser.displayName}!`);
      navigate('/resume-tool');
    } catch (error) {
      console.error('Error signing in:', error);
      alert('Failed to sign in. Please try again.');
    }
  };

  // Redirect if the user is already logged in
  if (user) {
    navigate('/resume-tool');
    return null; // Prevent rendering of the login page
  }

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        height: '40vh',
        textAlign: 'center',
      }}
    >
      <Typography variant="h4" gutterBottom>
        Create Your Account
      </Typography>
      <Typography variant="body1" gutterBottom>
        Start using our AI-powered resume-writing tool today!
      </Typography>
      <Button
        variant="contained"
        color="primary"
        size="large"
        sx={{ mt: 3 }}
        onClick={handleGoogleSignIn}
      >
        Sign in with Google
      </Button>
    </Box>
  );
};

export default LoginPage;
