import React from 'react';
import Book from'./Book.js';
import { useNavigate} from "react-router-dom";
import './MyBooks.scss';
import axios from 'axios';
import SimpleModal from './SimpleModal';

const MyBooks = () => {
    const[books, setBooks] = React.useState([]);
    const[showModal, setShowModal] = React.useState(false);
    const history = useNavigate()
    const fetchBooks=()=>{
      axios.get('/v1/books')
      .then(response =>{
        setBooks(response.data)
      })
    }
    React.useEffect(()=>{
      fetchBooks();
      },[]);

      const handleDelete = (bookId) =>{
        axios.delete(`/v1/books/${bookId}`).then(
          reponse =>{
            fetchBooks();
          }).catch(error=>{setShowModal(true)})
      }

      const handleCloseModal=()=>{
        setShowModal(false)
      }
      return (
        <>
           <div className="container"><h1>Mes Livres</h1>
              <div className="list-container ">
                {books.length===0? "Vous n'avez pas déclaré de livre":null}
                {books.map((book) => (
                  <div key={book.id} className="mybook-container " >
                    <Book title={book.title} category={book.category.label}/>
                    <div className="container-buttons ">
                      <button className="btn btn-primary btn-sm" onClick={()=>history(`/addBook/${book.id}`)}>Modifier</button>
                      <button className="btn btn-primary btn-sm" onClick={()=>handleDelete(book.id)}>Supprimer</button>
                    </div>
                  </div>
                ))}
              </div>
              <button className="btn btn-primary btn-sm" onClick={()=>history('/addBook')}>Ajouter</button>           
          </div>
          <SimpleModal title={"Supression de livre impossible"} bodyTxt={"Livre en cours d'emprunt"} 
          handleCloseModal={handleCloseModal} showModal={showModal}>

          </SimpleModal>
        </>
     )
}

export default  MyBooks