import React, { useContext, useEffect } from "react";
import {
  Container,
  Typography,
  Card,
  CardContent,
  CardActions,
  Button,
  Box,
  Grid,
  Avatar,
  Alert
} from "@mui/material";
import { UserContext } from "./UserContext";
import { useNavigate } from "react-router-dom";

const ProfilePage = () => {
  const { user, credits } = useContext(UserContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate("/signup");
    }
  }, [user, navigate]);

  const handleBuyCredits = () => {
    navigate("/payment");
  };

  return (
    <Container maxWidth="md" sx={{ mt: 5 }}>
      <Card variant="outlined" sx={{ p: 3, textAlign: "center" }}>
        <Box display="flex" justifyContent="center" alignItems="center" mb={3}>
          <Avatar
            alt={user?.displayName}
            sx={{ width: 100, height: 100, bgcolor: "primary.main", fontSize: 40 }}
          >
            {user?.displayName?.[0]?.toUpperCase() || "U"}
          </Avatar>
        </Box>
        <Typography variant="h5" gutterBottom>
          Welcome, {user?.displayName || "User"}!
        </Typography>
        <Typography variant="body1" color="text.secondary" gutterBottom>
          Working smarter with AI!
        </Typography>
        <Alert severity="info" sx={{ mt: 2, mb: 2 }}>
          You have <strong>{credits}</strong> AI Tokens remaining.
        </Alert>

        <Box>
          
          <Typography variant="h6" color="primary" gutterBottom>
            Special Offer: Buy more AI tokens!
          </Typography>
        </Box>

        <CardActions sx={{ justifyContent: "center", mt: 3 }}>
          <Button
            variant="contained"
            color="secondary"
            size="large"
            onClick={handleBuyCredits}
          >
            Buy Now
          </Button>
        </CardActions>
      </Card>

      <Grid container spacing={2} sx={{ mt: 5, mb: 5 }}>
        <Grid item xs={12} md={12}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Finding a Job in 2025!
              </Typography>
              <Typography variant="body1" color="text.secondary">
                - Resumes are your first impression in today's competitive job market.
                <br />- Employers spend an average of 6 seconds scanning each resume.
                <br />- Ensure yours highlights your strengths instantly.
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default ProfilePage;
