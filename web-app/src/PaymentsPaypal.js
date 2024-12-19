import React, { useContext, useEffect, useState, useRef } from "react";
import { PayPalScriptProvider, PayPalButtons } from "@paypal/react-paypal-js";
import {
  Typography,
  Box,
  Container,
  CssBaseline,
  Radio,
  RadioGroup,
  FormControlLabel,
  Grid,
  Paper,
  Divider,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { UserContext } from './UserContext';

const PaymentsPage = () => {
  const navigate = useNavigate();
  const [idToken, setIdToken] = useState(null);
  const [selectedProductId, setSelectedProductId] = useState(null);
  const selectedProductIdRef = useRef(selectedProductId);
  const { user, setCredits } = useContext(UserContext);

  useEffect(() => {
    if (!user) {
      navigate("/signup");
    }
  }, [user, navigate]);
      
  useEffect(() => {
    if (user) {
      const fetchIdToken = async () => {
        try {
          const token = await user.getIdToken();
          setIdToken(token);
        } catch (error) {
          console.error("Error fetching user ID token:", error);
        }
      };

      fetchIdToken();
    }
  }, [user]);

  useEffect(() => {
    selectedProductIdRef.current = selectedProductId;
  }, [selectedProductId]);

  if (!process.env.REACT_APP_PAYPAL_CLIENT_ID) {
    console.error("PayPal client ID is missing. Please check your .env file.");
  }

  // Initial PayPal configuration
  const initialOptions = {
    "client-id": `${process.env.REACT_APP_PAYPAL_CLIENT_ID}`,
    currency: "USD",
    intent: "capture",
    "buyer-country": "US",
    components: "buttons",
    "enable-funding": "venmo,paylater,card",
  };

  // These productIds need to match your Backend DynamoDB Ids and amounts.
  // Ideally you would call your backend to populate the data and not have products hardcoded here.
  const products = [
    {
      id: "bulkToken0",
      name: "30 AI Tokens",
      price: 9.99,
      description: "For recruiting season",
      recommended: true,
    },
    {
      id: "bulkTokenZ",
      name: "5 AI Tokens",
      price: 2.99,
      description: "For a one-time rewrite",
      recommended: false, 
    },
    
  ];

  const selectedProduct = products.find((product) => product.id === selectedProductId);

  const handleProductSelection = (event) => {
    setSelectedProductId(event.target.value);
  };

  const createOrder = async () => {
      
      if (!selectedProductIdRef.current) {
        alert("Please select a product before checking out.");
        return;
      }
      
      try {
          const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/products`, {
              method: "POST",
              headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${idToken}`,
              },
              body: JSON.stringify({
                cart: [
                  {
                      id: `${selectedProductIdRef.current}`,
                      quantity: "1",
                  },
                ],
              }),
          });

          const orderData = await response.json();

          // Check if response contains the expected order ID
          if (!orderData.orderId) {
              const errorDetail = orderData.details ? orderData.details[0] : null;
              const errorMessage = errorDetail
                  ? `${errorDetail.issue} ${errorDetail.description} (${orderData.debug_id || "No debug ID"})`
                  : "Unexpected error occurred, please try again.";

              throw new Error(errorMessage);
          }

          const { approvalLink, orderId } = orderData;
          console.log(`OrderId returned from backend: ${orderId}`);
          return orderId;

      } catch (error) {
          console.error("Error creating order:", error);
          throw error;
      }
  };

  const onApprove = async(data, actions) => {
    console.log("onApprove Data:", data);
    try {
        // Make API call to your backend to capture the order
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/products-capture`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${idToken}`,
            },
            body: JSON.stringify({
              orderId: `${data.orderID}`,
            }),
        });

        const orderData = await response.json();

        const errorDetail = orderData?.details?.[0];

        if (errorDetail?.issue === "INSTRUMENT_DECLINED") {
            // Case 1: Recoverable error, ask the user to retry
            return actions.restart();
        } else if (errorDetail) {
            // Case 2: Non-recoverable error, display an error message
            throw new Error(`${errorDetail.description} (${orderData.debug_id})`);
        } else if (!orderData.purchase_units) {
            // Case 3: If there's no purchase units in the response, handle it
            throw new Error("No purchase units found in the response.");
        } else {
            // Case 4: Successful transaction, display success message
            const transaction =
                orderData?.purchase_units?.[0]?.payments?.captures?.[0] ||
                orderData?.purchase_units?.[0]?.payments?.authorizations?.[0]; 
            const newCredits = orderData?.credits ?? 0;

            console.log(`AI tokens: ${newCredits}`);

            setCredits(newCredits);

            const resultMessage = `Transaction ${transaction.status}: ${transaction.id}`;

            console.log(resultMessage); 
            alert(`Payment successful! Transaction id: ${transaction.id}`);

            // Optionally, redirect the user to a thank you page
            // actions.redirect("thank_you_page.html");
        }
    } catch (error) {
        console.error("Error in onApprove:", error);
        alert(`Sorry, your transaction could not be processed. Error: ${error.message}`);
    }
  }

  return (
    <>
      
      <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={4}>
        {/* Left Side: Product Selection */}
        <Grid item xs={12} md={8}>
          <Typography variant="h4" gutterBottom>
          Get Started in Under 60 Seconds!
          </Typography>
          <RadioGroup
            value={selectedProductId || ""}
            onChange={handleProductSelection}
            sx={{ mt: 2 }}
          >
            {products.map((product) => (
              <Paper
              key={product.id}
              elevation={selectedProductId === product.id ? 4 : 1}
              sx={{
                p: 2,
                mb: 2,
                border: selectedProductId === product.id ? "2px solid #1976d2" : "1px solid #e0e0e0",
                position: "relative", // Enables positioning for the recommended badge
                borderRadius: "8px",
                overflow: "hidden",
              }}
            >
              {/* Recommended Badge */}
              {product.recommended && (
                <Box
                  sx={{
                    position: "absolute",
                    top: 0,
                    left: 0,
                    backgroundColor: "#1976d2",
                    color: "#fff",
                    px: 2,
                    py: 0.5,
                    fontSize: "0.75rem",
                    fontWeight: "bold",
                    textTransform: "uppercase",
                    borderBottomRightRadius: "8px",
                  }}
                >
                  Recommended
                </Box>
              )}
            
              {/* Product Details */}
              <Box sx={{ pt: product.recommended ? 2 : 0 }} >
              <FormControlLabel
                value={product.id}
                control={<Radio />}
                label={
                  <Box >
                    <Typography variant="h6" sx={{ fontWeight: "bold" }}>
                      {product.name}
                    </Typography>
                    <Typography variant="body2" color="textSecondary" sx={{ mt: 0.5 }}>
                      {product.description}
                    </Typography>
                    <Typography variant="h6" color="primary" sx={{ mt: 1 }}>
                      ${product.price.toFixed(2)}
                    </Typography>
                  </Box>
                }
                sx={{ alignItems: "flex-start", m: 0 }}
              />
              </Box>
            </Paper>
            ))}
          </RadioGroup>
        </Grid>

        {/* Right Side: Order Summary */}
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h5" gutterBottom>
              Order Summary
            </Typography>
            <Divider sx={{ my: 2 }} />
            {selectedProduct ? (
              <Box>
                <Typography variant="body1">{selectedProduct.name}</Typography>
                <Typography variant="body2" color="textSecondary">
                  {selectedProduct.description}
                </Typography>
                <Typography variant="h6" sx={{ mt: 2 }}>
                  Total: ${selectedProduct.price.toFixed(2)}
                </Typography>
              </Box>
            ) : (
              <Typography variant="body1" color="textSecondary">
                No product selected.
              </Typography>
            )}
            <Divider sx={{ my: 2 }} />
            <PayPalScriptProvider options={initialOptions}>
              <Box sx={{ mt: 2 }}>
                <Typography
                  variant="body2"
                  align="center"
                  color="textSecondary"
                  sx={{ mb: 2 }}
                >
                  Secure payment with PayPal
                </Typography>
                <PayPalButtons
                  style={{
                    shape: "rect",
                    layout: "vertical",
                    color: "gold",
                    label: "paypal",
                    size: "responsive",
                  }}
                  createOrder={createOrder}
                  onApprove={onApprove}
                />
              </Box>
            </PayPalScriptProvider>
          </Paper>
        </Grid>
      </Grid>
    </Container>


      
    </>
  );
};

export default PaymentsPage;
