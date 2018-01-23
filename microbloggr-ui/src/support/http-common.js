import axios from 'axios';

export const HTTP = axios.create({
  baseURL: `http://localhost:8000`,
  headers: {
    'X-Session-Id': 'ae7823a0-57ba-49a6-a4c3-8f974e141048'
  },
  responseType: 'json'
});
