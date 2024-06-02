import axios from 'axios';

const PEXELS_API_KEY = 'BmUumOxaOZaKluyQYetVse4DW0HzriKAfzwqilj48Sy9EztyIcFwC66q';
const PEXELS_API_URL = 'https://api.pexels.com/v1/search';

export const fetchImage = async (query) => {
  try {
    const response = await axios.get(PEXELS_API_URL, {
      headers: {
        Authorization: PEXELS_API_KEY
      },
      params: {
        query,
        per_page: 1 // Limit to one image per query
      }
    });
    return response.data.photos[0]?.src.medium || null; // Return the medium-sized image URL or null if no image found
  } catch (error) {
    console.error('Error fetching images from Pexels:', error);
    return null;
  }
};