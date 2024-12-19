import React, { useContext } from 'react';
import { AppBar, Toolbar, Typography, Box, Avatar, IconButton, Button } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';
import { signOut } from 'firebase/auth';
import { auth } from './firebaseConfig'; 
import { UserContext } from './UserContext';

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation(); // Get current location to determine the page
  const { user } = useContext(UserContext);

  const handleLogout = () => {
    signOut(auth)
      .then(() => {
        navigate('/'); // Navigate to home page after logout
      })
      .catch((error) => {
        console.error('Error logging out:', error);
        alert('Failed to log out.');
      });
  };

  const handleLogoClick = () => {
    navigate('/'); 
  };

  const handleProfileClick = () => {
    navigate('/profile'); 
  };

  return (
    <AppBar position="static" sx={{ bgcolor: 'primary.main' }}>
      <Toolbar>
        <Typography
          variant="h6"
          sx={{ flexGrow: 1, cursor: 'pointer' }}
          onClick={handleLogoClick} // Handle click on the logo
        >
          ResumeAI
        </Typography>

        {/* Show Login button only if the user is not logged in */}
        {location.pathname !== '/signup' && !user && (
          <Button color="inherit" onClick={() => navigate('/signup')}>
            Login
          </Button>
        )}

        {/* If the user is logged in, show the avatar and logout button */}
        {user && (
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <IconButton onClick={handleProfileClick}>
              <Avatar
                alt={user.displayName || 'Profile'}
                src={user.photoURL} // Use the user's photo URL
                sx={{ width: 40, height: 40, cursor: 'pointer' }}
              />
            </IconButton>
            <IconButton
              color="inherit"
              onClick={handleLogout}
              sx={{ ml: 2, fontSize: '0.9rem' }}
            >
              Logout
            </IconButton>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Header;
