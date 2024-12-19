import React from 'react';
import { Typography, Button, Box, Container, Grid, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';

// Inside LandingPage component:



const LandingPage = () => {
  const navigate = useNavigate();
  return (
    <>

      {/* Hero Section */}
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          height: '70vh',
          textAlign: 'center',
          background: 'linear-gradient(to bottom right, #1976d2, #42a5f5)',
          color: '#fff',
        }}
      >
        <Typography variant="h2" gutterBottom>
          Build Your Dream Resume in Minutes
        </Typography>
        <Typography variant="h6" gutterBottom>
          Let AI take your resume to the next level. Optimize, format, and shine.
        </Typography>
        <Button
          variant="contained"
          color="secondary"
          size="large"
          sx={{ mt: 3 }}
          onClick={() => navigate('/signup')}
        >
          Get Started
        </Button>
      </Box>

      {/* Features Section */}
      <Container sx={{ py: 5 }}>
        <Typography variant="h4" align="center" gutterBottom>
          Features
        </Typography>
        <Grid container spacing={4}>
          {[
            { title: 'AI-Powered Suggestions', description: 'Get instant feedback and improvement tips for your resume.' },
            { title: 'Customizable Templates', description: 'Choose from a variety of modern, professional designs.' },
            { title: 'Keyword Optimization', description: 'Tailor your resume for the job you want.' },
          ].map((feature, index) => (
            <Grid item xs={12} sm={4} key={index}>
              <Paper elevation={3} sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="h6" gutterBottom>
                  {feature.title}
                </Typography>
                <Typography>{feature.description}</Typography>
              </Paper>
            </Grid>
          ))}
        </Grid>
      </Container>

      {/* How It Works Section */}
      <Container sx={{ py: 5 }}>
        <Typography variant="h4" align="center" gutterBottom>
          How It Works
        </Typography>
        <Grid container spacing={4}>
          {['Sign Up', 'Upload Your Info', 'Generate Your Resume'].map((step, index) => (
            <Grid item xs={12} sm={4} key={index}>
              <Paper elevation={3} sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="h6" gutterBottom>
                  Step {index + 1}: {step}
                </Typography>
                <Typography>Follow the simple steps to get started.</Typography>
              </Paper>
            </Grid>
          ))}
        </Grid>
      </Container>

      {/* Testimonials Section */}
      <Box sx={{ py: 5, backgroundColor: '#f5f5f5' }}>
        <Container>
          <Typography variant="h4" align="center" gutterBottom>
            What Our Users Say
          </Typography>
          <Typography align="center" sx={{ fontStyle: 'italic', maxWidth: '600px', mx: 'auto' }}>
            "ResumeGenie helped me land my dream job with its easy-to-use interface and expert recommendations."
          </Typography>
          <Typography align="center" sx={{ mt: 2 }}>
            - Jane Doe, Marketing Manager
          </Typography>
        </Container>
      </Box>

      
    </>
  );
};

export default LandingPage;
