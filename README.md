# Zignsec.MobileSdk.iOS

Mobile SDK for Online Document Scanning and Liveness through Zignsec. 

## Usage

1. Through your Backend, obtain an Access Token and Session ID By calling the ZignSec API Endpoint  https://test-gateway.zignsec.com/core/api/sessions/scanning/mobile (for Test Sessions) or https://gateway.zignsec.com/core/api/sessions/scanning/mobile for production Sessions. 
1. Transport the Access Token and Session ID from your backend to the Mobile App. 
1. Create a ZignSecIdentificationCompletion callback to handle results of the sessions.
1. Create a ZignSecIdentificationActivity, providing the Zignsec Environment (Test or Prod) together with the session id and access token which your backend would have obtained through the ZignSec API.
1. Call startIdentification on the instance created in (b), passing the context and completion function created in (a). This will render the camera to the user and ask him/her to perform the desired actions (as triggered by the backend in the initial call). On completion your callback will be called with the results of the session.

