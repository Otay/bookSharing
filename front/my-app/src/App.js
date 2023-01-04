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
            return request
          }, (error)=>{
            return Promise.reject(error);
          });
  })

  const [userInfo, setUserInfo] = React.useState('');
  
  return (
    <div>
      <UserConnected userInfo={userInfo} setUserInfo={setUserInfo} />
     <div className="App">
        <Routes>
          <Route path="listBooks" element={<ListBooks/>}/>
          <Route path="myBooks" element={<MyBooks/>}/>
          <Route path="addBook" element={<AddBook/>}/>
          <Route path="addBook/:bookId" element={<AddBook/>}/>
          <Route path="myBorrows" element={<MyBorrows/>}/>
          <Route path="addUser" element={<AddUser setUserInfo={setUserInfo}/>}/>
          <Route path="*" element={<Login setUserInfo={setUserInfo}/>}/>
        </Routes>
      </div>
      {/*
      <Label color="red" label="My solution"/>*/
      }
    </div>
  );
}

export default App;
