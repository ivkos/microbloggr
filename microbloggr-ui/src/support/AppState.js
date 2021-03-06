const AppState = {
  get sessionId() {
    return this.get('sessionId');
  },

  set sessionId(val) {
    this.set('sessionId', val);
  },

  get userId() {
    return this.get('userId');
  },

  set userId(val) {
    this.set('userId', val);
  },

  get user() {
    const data = this.get('user');
    return data ? JSON.parse(data) : undefined;
  },

  set user(val) {
    this.set('user', JSON.stringify(val));
  },

  get isAdmin() {
    return this.get('isAdmin') === "true";
  },

  set isAdmin(val) {
    this.set('isAdmin', val);
  },

  get(prop) {
    return localStorage.getItem(prop) || undefined;
  },

  set(prop, val) {
    localStorage.setItem(prop, val);
  },

  clear() {
    localStorage.clear();
  }
};

export default AppState;
