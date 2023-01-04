import {useNavigate, useParams} from 'react-router-dom'
import React from 'react'
import './AddBook.scss'
import axios from 'axios'

export default function AddBook () {
    let{bookId} = useParams();
    const [bookData, setBookData] = React.useState({});
    const [categoriesData, setCategoriesData] = React.useState([]);
    React.useEffect(()=>
        {
            axios.get('/v1/categories').then(response=>{
                            setCategoriesData(response.data)
                            setBookData({
                                            title:'',
                                            categoryId:response.data[0].id
                                        })
            })
            .then(()=>{
                if(bookId){
                    axios.get(`/v1/books/${bookId}`).then(response=>{
                        setBookData({title:response.data.title,
                            categoryId: response.data.category.id})
                    } ) 
                }
        
            })
        },[bookId]
    )
    const goTo=useNavigate();
    const onSubmit = (event)=>{
        event.preventDefault();
        console.log('onSubmit');
        console.log(bookData);

        if(bookId){
            axios.put(`/v1/books/${bookId}`,{...bookData})
            .then(()=>{
                goTo('/MyBooks')
            })
        }else{
            axios.post('/v1/books',{...bookData})
            .then(()=>{
                goTo('/MyBooks')
            })
        }
    }
    const handleChange = (event) =>{
        let currentState = {...bookData};
        currentState[event.target.name]= event.target.value;
        setBookData(currentState)
    }
    
    
        return ( <div className="container">
            <div className="container-add-book">
                <h1>Ajouter un livre</h1>
                <form onSubmit={onSubmit}>
                    <div>
                        <label>
                            Nom du livre
                        </label>
                        <input name="title" type="text" value={bookData.title} onChange={handleChange} className="form-control">

                        </input>
                    </div>
                    <div>
                        <label>
                            Catégory du livre
                        </label>
                        <select name="categoryId" value={bookData.categoryId} className="form-select" onChange={handleChange}>
                            {
                                categoriesData.map( category=>(
                                    <option value={category.id} key={category.id}>{category.label}</option>)
                            )}
                        </select>
                    </div>
                    <div className="containe-submit"> 
                        <input type="submit" value="Validate" className="btn btn-primary btn-sm"></input>
                    </div>

                </form>
            </div>
            </div>
        )
}