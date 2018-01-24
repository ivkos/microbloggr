import axios from 'axios';
import AppState from "./AppState";

function getHeaders() {
  const headers = {};

  if (AppState.sessionId) {
    headers['X-Session-Id'] = AppState.sessionId;
  }

  return headers;
}

export const HTTP = axios.create({
  baseURL: `http://localhost:8000`,
  headers: getHeaders(),
  responseType: 'json'
});
