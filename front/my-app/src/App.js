import AddBook from'./AddBook.js';
import ListBooks from'./ListBooks.js';
import MyBooks from'./MyBooks.js';
import MyBorrows from'./MyBorrows.js';
import Login from'./Login.js';
import AddUser from'./AddUser.js';
import Header from'./Header.js';

import {Route,Routes, useNavigate, useLocation} from 'react-router-dom'
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';
import React from 'react';
import Spinner from 'react-bootstrap/Spinner'
import './App.scss'

export const AUTH_TOKEN_KEY = 'jhi-authenticationToken';

const UserConnected = ({ setUserInfo, userInfo }) => {
  const history = useNavigate();
  let location = useLocation();

  React.useEffect(() => {
    setUserInfo(null)
    axios.get('/isConnected').then(response => {
      setUserInfo(response.data)
    }, () => {
      if (location.pathname !== '/addUser') {
        history("/login")
      }
    })
  }, [history, setUserInfo, location.pathname]);

  return (<>
    {userInfo && <Header userInfo={userInfo} setUserInfo={setUserInfo} />}
  </>)
}

function App() {

  React.useEffect(()=>{
          axios.interceptors.request.use(function(request){
            const token = sessionStorage.getItem(AUTH_TOKEN_KEY)
            if(token){
              request.headers.Authorization = `Bearer ${token}`
            }
            setLoading(true)
            return request
          }, (error)=>{
            setLoading(false)
              return Promise.reject(error);
            });

            axios.interceptors.response.use(function (response) {
              setLoading(false)
              return response;
            }, (error) => {
              setLoading(false)
            return Promise.reject(error);
          });
  })

  const [userInfo, setUserInfo] = useState('');
  const [loading, setLoading] = useState(false)
  
  return (
    <div>
      {loading && (
        <div className="background-spinner">
          <div className="spinner">
            <Spinner animation="grow" variant="light" />
          </div>
        </div>
      )}
      <UserConnected userInfo={userInfo} setUserInfo={setUserInfo} />
      <div className="App">
        <Routes>
          <Route path="listBooks" element={<ListBooks />} />
          <Route path="myBooks" element={<MyBooks />} />
          <Route path="addBook" element={<AddBook />} />
          <Route path="addBook/:bookId" element={<AddBook />} />
          <Route path="myBorrows" element={<MyBorrows />} />
          <Route path="addUser" element={<AddUser setUserInfo={setUserInfo} />} />
          <Route path="*" element={<Login setUserInfo={setUserInfo} />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;
