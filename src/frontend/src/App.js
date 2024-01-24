import './css/App.css';
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Authentification from './pages/Authentification.js';
import Home from './pages/Home.js';

function App() {
  return (
    <div>
      <Router>
        <Routes>
          <Route path='/' exact Component={Authentification} />
          <Route path='/home' exact Component={Home} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
