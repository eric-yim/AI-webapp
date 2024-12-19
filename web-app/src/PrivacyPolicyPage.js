import React from 'react';
import { Box, Typography, Link } from '@mui/material';

const PrivacyPolicyPage = () => {
  return (
    <Box sx={{ padding: 4 }}>
      <Typography variant="h4" gutterBottom>
        Privacy Policy
      </Typography>

      <Typography variant="body1" paragraph>
        At ResumeGenie, we value your privacy and are committed to protecting the personal data you share with us. This Privacy Policy outlines how we collect, use, store, and protect your information when you use our services. By using our website, you agree to the terms outlined in this policy.
      </Typography>

      <Typography variant="h6" gutterBottom>
        1. Information We Collect
      </Typography>
      <Typography variant="body1" paragraph>
        We collect personal information that you provide to us when you use our website. The types of personal information we collect include:
      </Typography>
      <ul>
        <li><Typography variant="body1">Email address</Typography></li>
        <li><Typography variant="body1">Name</Typography></li>
        <li><Typography variant="body1">Profile picture URL (through third-party authentication systems)</Typography></li>
      </ul>

      <Typography variant="h6" gutterBottom>
        2. How We Collect Your Information
      </Typography>
      <Typography variant="body1" paragraph>
        We collect your information through third-party authentication systems that allow you to log in to our website. This may include email addresses and names associated with your authentication account.
      </Typography>

      <Typography variant="h6" gutterBottom>
        3. How We Use Your Information
      </Typography>
      <Typography variant="body1" paragraph>
        We use your personal information to provide authentication and connect your account to our credit system for webpage usage. Specifically, we use your email address and name for:
      </Typography>
      <ul>
        <li><Typography variant="body1">Authentication and account management</Typography></li>
        <li><Typography variant="body1">Connecting your account with our credit system</Typography></li>
        <li><Typography variant="body1">Improving website functionality and performance</Typography></li>
        <li><Typography variant="body1">Sending notifications related to your account and usage of the website</Typography></li>
      </ul>

      <Typography variant="h6" gutterBottom>
        4. Sharing Your Information
      </Typography>
      <Typography variant="body1" paragraph>
        We do not share your personal information with third parties. However, our website may contain redirect links that take you to third-party payment fulfillment sites for processing payments. We do not collect any payment details directly; these are handled by trusted third-party payment processors.
      </Typography>

      <Typography variant="h6" gutterBottom>
        5. How We Protect Your Information
      </Typography>
      <Typography variant="body1" paragraph>
        We take the protection of your data seriously. To safeguard your information, we implement the following measures:
      </Typography>
      <ul>
        <li><Typography variant="body1">Access controls</Typography></li>
        <li><Typography variant="body1">Secure transfer protocols</Typography></li>
        <li><Typography variant="body1">Encryption at rest</Typography></li>
      </ul>

      <Typography variant="h6" gutterBottom>
        6. Cookies and Tracking Technologies
      </Typography>
      <Typography variant="body1" paragraph>
        We may use cookies or similar tracking technologies to anonymously understand webpage traffic and improve your experience on our website. These technologies do not collect personally identifiable information.
      </Typography>

      <Typography variant="h6" gutterBottom>
        7. Third-Party Advertising
      </Typography>
      <Typography variant="body1" paragraph>
        We do not allow third-party advertisers or tracking services to track users on our website for the purposes of targeted advertising.
      </Typography>

      <Typography variant="h6" gutterBottom>
        8. User Control and Data Retention
      </Typography>
      <Typography variant="body1" paragraph>
        You have the right to opt out of receiving promotional emails from us. To do so, simply click the unsubscribe link at the bottom of any of our emails.
      </Typography>
      <Typography variant="body1" paragraph>
        We retain your personal data for a minimum of two years. If you wish to delete your account or request the deletion of your data, please contact us, and we will remove your information.
      </Typography>

      <Typography variant="h6" gutterBottom>
        9. Your Rights and Contact Information
      </Typography>
      <Typography variant="body1" paragraph>
        If you have any questions or concerns regarding this Privacy Policy or how your data is handled, please feel free to contact us through our <Link href="/contact" color="primary">contact page</Link>.
      </Typography>

      <Typography variant="h6" gutterBottom>
        10. Changes to This Privacy Policy
      </Typography>
      <Typography variant="body1" paragraph>
        We may update this Privacy Policy from time to time. If we make any significant changes, we will notify you by email. Please check this page periodically for the latest updates.
      </Typography>

      <Typography variant="body1" paragraph>
        Last updated: December 2024
      </Typography>
    </Box>
  );
};

export default PrivacyPolicyPage;