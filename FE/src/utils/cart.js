// src/utils/cart.js
const CART_KEY = "cart_items";

const getCart = () => {
  return JSON.parse(localStorage.getItem(CART_KEY)) || [];
};

const saveCart = (cart) => {
  localStorage.setItem(CART_KEY, JSON.stringify(cart));
};

export const addItem = (book, qty) => {
  const cart = getCart();
  const exist = cart.find(i => i.id === book.id);

  if (exist) {
    exist.qty += qty;
  } else {
    cart.push({
      id: book.id,
      name: book.tieuDe,
      price: book.gia,
      qty,
      image: book.mainImage 
    });
  }

  saveCart(cart);
};

export const getItems = () => getCart();

export const removeItem = (id) => {
  saveCart(getCart().filter(i => i.id !== id));
};

export const updateQty = (id, qty) => {
  const cart = getCart();
  const item = cart.find(i => i.id === id);
  if (item) item.qty = qty;
  saveCart(cart);
};

export const clearCart = () => {
  localStorage.removeItem(CART_KEY);
};
