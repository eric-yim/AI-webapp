import React from 'react';
import { Typography, Box, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const Footer = () => {
    const navigate = useNavigate(); // Initialize the navigate function

    const handleNavigation = (path) => {
      navigate(path); // Navigate to the given path
    };

    return ( // Return statement is needed here
        <Box sx={{ py: 3, backgroundColor: '#1976d2', color: '#fff', textAlign: 'center' }}>
            <Typography variant="body2">
                &copy; 2024 ResumeGenie. All rights reserved.
            </Typography>
            <Typography variant="body2">
                <Button color="inherit" onClick={() => handleNavigation('/privacy-policy')}>Privacy Policy</Button> | 
                <Button color="inherit" onClick={() => handleNavigation('/terms-of-service')}>Terms of Service</Button>
            </Typography>
        </Box>
    );
};

export default Footer;