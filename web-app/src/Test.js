import React, { useState } from "react";
import { PayPalScriptProvider, PayPalButtons } from "@paypal/react-paypal-js";
import {
  Box,
  Container,
  Grid,
  Typography,
  Paper,
  Divider,
  RadioGroup,
  FormControlLabel,
  Radio,
} from "@mui/material";

const CheckoutPage = () => {
  const [selectedProductId, setSelectedProductId] = useState(null);

  const initialOptions = {
    "client-id": `${process.env.REACT_APP_PAYPAL_CLIENT_ID}`,
    currency: "USD",
    intent: "capture",
    "buyer-country": "US",
    components: "buttons",
    "enable-funding": "venmo,paylater,card",
  };

  const products = [
    {
      id: 1,
      name: "Product A",
      price: 49.99,
      description: "A high-quality item.",
      recommended: false,
    },
    {
      id: 2,
      name: "Product B",
      price: 79.99,
      description: "A premium option.",
      recommended: true, // Highlight this as recommended
    },
    {
      id: 3,
      name: "Product C",
      price: 29.99,
      description: "A budget-friendly choice.",
      recommended: false,
    },
  ];

  const selectedProduct = products.find((product) => product.id === selectedProductId);

  const handleProductSelection = (event) => {
    setSelectedProductId(Number(event.target.value));
  };

  const createOrder = async () => {
    // Logic for creating the PayPal order
  };

  const onApprove = async (data, actions) => {
    // Logic for approving the payment
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={4}>
        {/* Left Side: Product Selection */}
        <Grid item xs={12} md={8}>
          <Typography variant="h4" gutterBottom>
            Choose Your Product
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
              <FormControlLabel
                value={product.id}
                control={<Radio />}
                label={
                  <Box>
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
  );
};

export default CheckoutPage;
