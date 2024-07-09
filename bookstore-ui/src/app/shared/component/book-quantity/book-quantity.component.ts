import {Component, inject, Input, OnInit} from '@angular/core';
import {Book} from "../../model/book";
import {Observable} from "rxjs";
import {ShoppingCart} from "../../model/shopping-cart";
import {ShoppingCartService} from "../../service/shopping-cart.service";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-book-quantity',
  standalone: true,
  imports: [
    CommonModule,
  ],
  templateUrl: './book-quantity.component.html',
  styleUrl: './book-quantity.component.css'
})
export class BookQuantityComponent implements OnInit {
  @Input("book") book: Book | undefined;
  @Input("shopping-cart") shoppingCart$: Observable<ShoppingCart | undefined> | undefined;
  shoppingCart: ShoppingCart | undefined;
  private shoppingCartService = inject(ShoppingCartService);

  ngOnInit(): void {
    this.shoppingCart$?.subscribe(cart => this.shoppingCart = cart);
  }

  addToCart(cartId: string) {
    if (this.shoppingCart && this.book && this.book.isbn) {
      const body = {
        cartId: cartId,
        isbn: this.book.isbn,
        inventory: this.book.inventory,
        shoppingCart: this.shoppingCart
      };
      this.shoppingCartService.incrementCartItem(body)
    } else {
      console.log("Book#isbn is error")
    }
  }

  removeFromCart(cartId: string) {
    if (this.shoppingCart && this.book && this.book.isbn) {
      const body = {
        cartId: cartId,
        isbn: this.book.isbn,
        inventory: this.book.inventory,
        shoppingCart: this.shoppingCart
      };
      this.shoppingCartService.decrementCartItem(body)
    } else {
      console.log("Book#isbn is error")
    }
  }

}
